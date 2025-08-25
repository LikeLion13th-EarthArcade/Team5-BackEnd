package com.project.team5backend.domain.space.space.repository;

import com.project.team5backend.domain.space.space.entity.Space.Status;
import com.project.team5backend.domain.space.space.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpaceRepositoryImpl implements SpaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Space> findSpacesWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            String district,
            SpaceSize size,
            SpaceType type,
            SpaceMood mood,
            Pageable pageable
    ) {
        QSpace space = QSpace.space;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder()
                .and(space.isDeleted.isFalse())
                .and(space.status.eq(Status.APPROVED)); // 필요시 상태 체크

        // 날짜 조건
        if (startDate != null) {
            builder.and(space.startDate.loe(startDate));
        }
        if (endDate != null) {
            builder.and(space.endDate.goe(endDate));
        }

        // 지역 조건
        if (district != null && !district.trim().isEmpty()) {
            builder.and(space.address.district.equalsIgnoreCase(district.trim()));
        }

        // 사이즈 조건
        if (size != null) {
            builder.and(space.size.eq(size));
        }

        // 타입 조건
        if (type != null) {
            builder.and(space.type.eq(type));
        }

        // 분위기 조건
        if (mood != null) {
            builder.and(space.mood.eq(mood));
        }

        // 전체 개수 조회
        Long total = queryFactory
                .select(space.count())
                .from(space)
                .where(builder)
                .fetchOne();

        // 정렬 (디폴트: id 오름차순)
        OrderSpecifier<?> order = space.id.asc();

        // 페이징된 결과 조회
        List<Space> content = queryFactory
                .selectFrom(space)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
